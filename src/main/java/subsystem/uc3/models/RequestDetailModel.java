package subsystem.uc3.models;

import entity.uc3.RequestDetailViewModel;

public class RequestDetailModel {
    private RequestDetailViewModel viewModel;
    private String keyword;

    public RequestDetailViewModel getViewModel() { return viewModel; }
    public void setViewModel(RequestDetailViewModel viewModel) { this.viewModel = viewModel; }
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }
}
