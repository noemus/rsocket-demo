import asyncio

from rsocket.extensions.mimetypes import WellKnownMimeTypes
from rsocket.rsocket_client import RSocketClient
from rsocket.transports.tcp import TransportTCP
from rsocket.helpers import single_transport_provider

from client import SolaceClient


async def main():
    connection = await asyncio.open_connection('localhost', 7000)
    async with RSocketClient(single_transport_provider(TransportTCP(*connection)),
                             metadata_encoding=WellKnownMimeTypes.MESSAGE_RSOCKET_COMPOSITE_METADATA) as client:
        sol_client = SolaceClient(client)
        await sol_client.send_string('send/topic/a', 'Fire Hello')
        await sol_client.subscribe_string('receive/topic/b', 3)
        await sol_client.unsubscribe()


if __name__ == '__main__':
    asyncio.run(main())
