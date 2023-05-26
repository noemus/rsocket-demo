from dataclasses import dataclass

from pure_protobuf.dataclasses_ import field, message
from pure_protobuf.types import int32


@message
@dataclass
class SearchRequest:
    query: str = field(1, default='')
    page_number: int32 = field(2, default=int32(0))
    result_per_page: int32 = field(3, default=int32(0))