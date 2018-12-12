import React, { Component, Fragment } from 'react'
import ReactPaginate from 'react-paginate'
import Icon from '~/components/icon/Icon'

import './Pagination.less'

const ITEM_PER_PAGE = 10

export default class Pagination extends Component {
	constructor(props) {
		super()
		this.state = {
			currentPage: 0,
			pageCount: Math.ceil(props.items.length / ITEM_PER_PAGE),
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
		const { items } = this.props

		const currentIndex = currentPage * ITEM_PER_PAGE + 1
		const lastIndex = currentIndex + ITEM_PER_PAGE - 1
		const itemCount = items.length
		const renderPagination = itemCount > ITEM_PER_PAGE

		return (
			<Fragment>
				{renderPagination && (
					<div className="pagination-wrapper">
						<span className="pagination-label">
							Viser {currentIndex}-{lastIndex > itemCount ? itemCount : lastIndex} av {items.length}
						</span>
						<ReactPaginate
							containerClassName="pagination-container"
							initialPage={currentPage}
							pageCount={pageCount}
							pageRangeDisplayed={2}
							marginPagesDisplayed={1}
							onPageChange={this._pageChangeHandler}
							previousLabel={<Icon kind="arrow-left" />}
							nextLabel={<Icon kind="arrow-right" />}
						/>
					</div>
				)}
				{this.props.render(currentItems)}
			</Fragment>
		)
	}
}
