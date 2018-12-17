import React, { Component, Fragment } from 'react'
import ReactPaginate from 'react-paginate'
import Icon from '~/components/icon/Icon'
import Select from 'react-select'

import './Pagination.less'

const ITEM_PER_PAGE = 10

export default class Pagination extends Component {
	constructor(props) {
		super()
		this.state = {
			currentPage: 0
		}
	}

	componentDidUpdate(prevProps) {
		if (prevProps.search !== this.props.search) return this.setState({ currentPage: 0 })
	}

	_pageChangeHandler = e => {
		this.setState({
			currentPage: e.selected
		})
	}

	_calculatePageCount = () => {
		return Math.ceil(this.props.items.length / ITEM_PER_PAGE)
	}

	_calculateItemsToRender = () => {
		const startIndex = this._calculateStartIndex()
		return this.props.items.slice(startIndex, startIndex + ITEM_PER_PAGE)
	}

	_calculateStartIndex = () => {
		return this.state.currentPage * ITEM_PER_PAGE
	}

	render() {
		const pageCount = this._calculatePageCount()
		const itemsToRender = this._calculateItemsToRender()

		const startIndex = this._calculateStartIndex() + 1
		const lastIndex = this._calculateStartIndex() + ITEM_PER_PAGE
		const itemCount = this.props.items.length
		const renderPagination = itemCount > ITEM_PER_PAGE

		const paginationComponent = (
			<div className="pagination-wrapper">
				<span className="pagination-label">
					Viser {startIndex}-{lastIndex > itemCount ? itemCount : lastIndex} av {itemCount}
				</span>
				<ReactPaginate
					containerClassName="pagination-container"
					forcePage={this.state.currentPage}
					pageCount={pageCount}
					pageRangeDisplayed={2}
					marginPagesDisplayed={1}
					onPageChange={this._pageChangeHandler}
					previousLabel={<Icon kind="arrow-left" />}
					nextLabel={<Icon kind="arrow-right" />}
				/>
			</div>
		)

		return (
			<Fragment>
				{renderPagination && paginationComponent}
				{this.props.render(itemsToRender)}
				{renderPagination && paginationComponent}
			</Fragment>
		)
	}
}
