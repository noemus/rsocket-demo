import json
import base64

from reactivestreams.subscriber import DefaultSubscriber
from reactivestreams.subscription import Subscription
from rsocket.extensions.helpers import composite, route
from rsocket.frame_helpers import ensure_bytes
from rsocket.helpers import utf8_decode
from rsocket.payload import Payload
from rsocket.rsocket_client import RSocketClient
from rsocket.rx_support.rx_rsocket import RxRSocket

from rx import operators


class SolaceClient:
    def __init__(self, rsocket: RSocketClient):
        self._rsocket = rsocket

    async def send_string(self, topic: str, data: str):
        json_data = _serialize({'topic': topic, 'data': data})
        payload = Payload(json_data, composite(route('send.string')))
        await self._rsocket.fire_and_forget(payload)

    async def send_bytes(self, topic: str, data: bytes):
        base64_data = base64.b64encode(data)
        json_data = _serialize({'topic': topic, 'encodedData': utf8_decode(base64_data)})
        payload = Payload(json_data, composite(route('send.bytes')))
        await self._rsocket.fire_and_forget(payload)

    async def subscribe_string(self, topic: str, count: int):
        payload = Payload(ensure_bytes(topic), composite(route('subscribe.string')))
        rx_client = RxRSocket(self._rsocket)
        await rx_client.request_stream(payload, count).pipe(
            operators.map(lambda _: _.data),
            operators.map(lambda _: utf8_decode(_)),
            operators.do_action(lambda _: print(f'Data: {_}'))
        )

    async def unsubscribe(self):
        payload = Payload(metadata=composite(route('unsubscribe')))
        await self._rsocket.fire_and_forget(payload)


class SolaceSubscriber(DefaultSubscriber):
    def __init__(self, count: int):
        super().__init__()
        self._count = count

    def on_subscribe(self, subscription: Subscription):
        subscription.request(self._count)

    def on_next(self, value, is_complete=False):
        print(f'Received: {utf8_decode(value)}')


def _serialize(message) -> bytes:
    return json.dumps(message).encode()
