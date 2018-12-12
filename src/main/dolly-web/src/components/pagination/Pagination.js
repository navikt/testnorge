import React, { Component, Fragment } from 'react'
import ReactPaginate from 'react-paginate'

const ITEM_PER_PAGE = 5

export default class Pagination extends Component {
	constructor(props) {
		super()
		this.state = {
			currentPage: 0,
			pageCount: Math.ceil((props.items.length + ITEM_PER_PAGE - 1) / ITEM_PER_PAGE),
			currentItems: props.items.slice(0, ITEM_PER_PAGE)
		}
	}

	_pageChangeHandler = e => {
		const { items } = this.props
		const { selected } = e

		const startIndex = selected * ITEM_PER_PAGE
		this.setState({
			currentPage: e.selected,
			currentItems: items.slice(startIndex, startIndex + ITEM_PER_PAGE)
		})
	}

	render() {
		const { currentItems, pageCount, currentPage } = this.state
		return (
			<Fragment>
				<ReactPaginate
					containerClassName="pagination-container"
					initialPage={currentPage}
					pageCount={pageCount}
					pageRangeDisplayed="2"
					marginPagesDisplayed="1"
					onPageChange={this._pageChangeHandler}
				/>
				{this.props.render(currentItems)}
			</Fragment>
		)
	}
}
