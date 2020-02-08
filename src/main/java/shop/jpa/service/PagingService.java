package shop.jpa.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class PagingService {

    private int allCount;   // 객체의 총 갯수

    private int unit;       // 노출할 객채수 (10개씩 노출)
    private int viewPage;   // 노출될 총 페이지수 (5페이지씩 노출)


    public List<?> getBoardPage(List<?> object, int currentPage, int unit, int viewPage, Model model) {
        this.unit = unit;
        this.viewPage = viewPage;

        this.allCount = object.size();

        int start = (currentPage - 1) * unit;
        int end = allCount;

        // 나머지가 있으면 start가 end와 같거나 큼
        if ((currentPage * unit) < end) {
           end = start + unit;
        }

        if(viewPage != 0) {
            this.setStartEndPage(currentPage, model);
        }

        return object.subList(start, end);

    }

    @Getter
    private int start;
    @Getter
    private int end;
    @Getter
    private int previous;
    @Getter
    private int next;
    @Getter
    private int maxPage;


    public void setStartEndPage(int currentPage, Model model) {
        // 모든 페이지 중 마지막 페이지
        maxPage = this.allCount / this.unit;
        int plusPage = this.allCount % this.unit;

        // 나머지가 값이 0보다 크면(나머지가 있으면) 페이지 추가 / 없으면 그대로
        if (plusPage > 0) {
            maxPage++;
        }

        // 나타낼 페이징의 수 : 5개씩 (1~5) -> viewPage = 5;
        // 1~5 페이지(1레벨) : 현재 페이지 / 6
        int firstPage = (currentPage / (this.viewPage + 1)) + 1;

        // 6이상 페이지
        if(currentPage > this.viewPage){
            if(currentPage % this.viewPage != 0){   // 나머지 값이 있을 때 = 레벨 유지
                firstPage = ((currentPage / this.viewPage) * this.viewPage) + 1;
            } else {    // 나머지 값이 없을 때 = 다음 레벨 직전 페이지
                firstPage = ((currentPage / this.viewPage) - 1) * this.viewPage + 1;
            }
        }

        int lastPage = firstPage + this.viewPage - 1;           // 첫 페이지 + 4가 마지막 페이지

        start = firstPage;
        end = lastPage;
        previous = start - unit + 1;
        next = end + 1;

        if (previous < 0) {
            previous = 1;
        }

        // 노출될 마지막 페이지의 값보다 총 마지막 페이지의 값이 작을 때 (1~5까지 노출하려 했지만 총 페이지 값이 3일 때)
        if (lastPage > maxPage) {
            end = maxPage; // 2까지만 노출
            next = end;
        }

        if(end == maxPage ) {
            next = maxPage;
        }

        model.addAttribute("resetPage", 1);
        model.addAttribute("previous", this.getPrevious());
        model.addAttribute("start", this.getStart());
        model.addAttribute("end", this.getEnd());
        model.addAttribute("next", this.getNext());
        model.addAttribute("lastPage", this.getMaxPage());
    }
}
