import React, {Component} from 'react';

class Pagination extends Component{
    constructor(props) {
        super(props);
        this.state = {
            pageItems: [],
            pageItemRefs: new Map(),
            page: 0,
            pageSize: 0,
            totalElements: 0,
            totalPages: 0,
            startPage: 1,
            endPage: 1,
            hasPrev: false,
            hasNext: false
        };
        this.setPaginationInfo = this.setPaginationInfo.bind(this);
        this.selectPage = this.selectPage.bind(this);
    }

    selectPage(num) {
        this.props.getTasksByPage(num);
    }

    setPaginationInfo(data) {
        let page = data.pageable.pageNumber + 1;
        let pageSize = data.pageable.pageSize;
        let totalElements = data.totalElements;
        let totalPages = data.totalPages;

        let startPage = parseInt((page / (5 + 1))) * 5 + 1;
        let endPage = startPage + 5 - 1;
        if(endPage > totalPages){
            endPage = totalPages;
        }

        let hasPrev = (startPage - 1) > 0;
        let hasNext = (endPage+1) <= totalPages;

        this.setState({
            page: page,
            pageSize: pageSize,
            totalElements: totalElements,
            totalPages: totalPages,
            startPage: startPage,
            endPage: endPage,
            hasPrev: hasPrev,
            hasNext: hasNext
        });

        var pageItems = [];
        let refSet = new Map();

        if (hasPrev) {
            pageItems.push(<span className="page"
                                 onClick={() => this.selectPage(startPage - 1)}
                                 key={Date.now() + 'p'}>
                            {'[prev] '}
                            </span>);
        }

        for (let num=startPage; num<=endPage; num++) {
            pageItems.push(<span className={num === page ? "selectedPage" : "page"}
                                 onClick={num === page ? null : () => this.selectPage(num)}
                                 key={Date.now() + num}
                                 ref={(el => refSet.set(num, el))}>
                            {'[' + num + '] '}
                            </span>);
        }

        if (hasNext) {
            pageItems.push(<span className="page" onClick={() => this.selectPage(endPage + 1)}
                            key={Date.now() + 'n'}>
                            {'[next]'}
                            </span>);
        }

        this.setState({pageItems: pageItems});
    }

    render() {
        var pageStyle = {
            width: '100%',
            height: '5%',
            textAlign: 'center',
            borderStyle: 'dotted',
            borderColor: 'rgba(255,255,255,.5)'
        };
        return (
            <div style={pageStyle}>
                {this.state.pageItems}
            </div>
        );
    }
}

export default Pagination;