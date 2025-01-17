import { SmallNextArrow, SmallPrevArrow } from "../Arrow/Arrow";
import { smallThumbnail } from "../../pages/Category/SeriesDummy";
import "./RecentWatching.css";
import Slider from "react-slick";
import { useNavigate } from "react-router-dom";
import { useSelector } from "react-redux";
import { RootState } from "../../store";
import { useQuery } from "react-query";
import { CarouselWatchedProps } from "./MainCarousel";
import { GetRecentWatching } from "./CarouselApI";
import Content from "../Container/Content";
export default function RecentWatching() {
  const userId = useSelector(
    (state: RootState) => state.tvUser.selectedProfile?.userId
  );

  const navigate = useNavigate();
  const {
    data: WatchingData,
    isLoading,
    error,
  } = useQuery<CarouselWatchedProps[]>(
    "watching",
    () => GetRecentWatching(userId!), // userId가 undefined가 아님을 보장
    {
      enabled: !!userId,
    }
  );
  if (isLoading)
    return (
      <div>
        <Content>Loading...</Content>
      </div>
    );

  let setting = {
    dots: true,
    infinite: false,
    slidesToShow: 4,
    slidesToScroll: 1,
    speed: 500,
    appendDots: (dots: any) => (
      <div
        style={{
          position: "absolute",
          display: "flex",
          alignItems: "center",
        }}
      >
        <ul> {dots} </ul>
      </div>
    ),
    dotsClass: "dots_custom",
    prevArrow: <SmallPrevArrow />,
    nextArrow: <SmallNextArrow />,
  };
  return (
    <div className="v-container">
      <div className="v-title">최근 시청중인 컨텐츠</div>
      <div className="thumbnail-container">
        {WatchingData && WatchingData.length > 0 ? (
          <Slider {...setting}>
            {WatchingData?.map((data, index) => (
              <div
                className="s-vid-container"
                key={index}
                style={{ position: "relative", transition: "all 0.3s" }}
                onClick={() => navigate(`/tv/stream/${data.detailId}`)}
              >
                <img src={data.thumbnailUrl} alt="콘텐츠 썸네일" />
                {data.episode ? (
                  <div className="vidTitle">
                    {data.title} - {data.episode}화
                  </div>
                ) : (
                  <div className="vidTitle">{data.title}</div>
                )}
              </div>
            ))}
          </Slider>
        ) : (
          <div className="no-content">현재 시청 중인 영상이 없습니다.</div>
        )}
      </div>
    </div>
  );
}
