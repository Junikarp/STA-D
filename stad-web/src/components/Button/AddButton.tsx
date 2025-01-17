import { useSelector } from "react-redux";
import React, { MouseEvent } from "react";

import { RootState } from "../../store";
import styles from "./Button.module.css";
import plus from "../../assets/ph_plus.png";
import { postBookmarkAdd } from "../../pages/Streaming/StreamingAPI";

export interface AddButtonProps {
  conceptId: number;
  onClick?: () => void;
}

const AddButton: React.FC<AddButtonProps> = ({ conceptId, onClick }) => {
  const token = useSelector((state: RootState) => state.token.accessToken);
  const userId = useSelector(
    (state: RootState) => state.tvUser.selectedProfile?.userId
  );

  const handleAddMark = (e: MouseEvent<HTMLButtonElement>) => {
    if (userId) {
      postBookmarkAdd(token, userId, conceptId);
    }
    if (onClick) onClick(); // onClick 함수가 존재하면 호출
  };

  return (
    <div className={`${styles.buttonWrapper}`}>
      <button className={`${styles.addButton}`} onClick={handleAddMark}>
        <img src={plus} alt="찜하기" />
      </button>
    </div>
  );
};

export default AddButton;
