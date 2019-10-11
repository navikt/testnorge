import React, { Component, Fragment } from 'react'
import ReactPaginate from 'react-paginate'
import Icon from '~/components/ui/icon/Icon'
import ItemCountSelect from './ItemCountSelect/ItemCountSelect'

import './Pagination.less'

const ITEM_PER_PAGE = 10

export default class Pagination extends Component {
	state = {
		currentPage: 0,
		itemCount: ITEM_PER_PAGE
	}

	componentDidUpdate(prevProps) {
		if (prevProps.items.length !== this.props.items.length) return this.setState({ currentPage: 0 })
	}

	render() {
		const pageCount = this._calculatePageCount()
		const itemsToRender = this._calculateItemsToRender()

		const startIndex = this._calculateStartIndex() + 1
		const lastIndex = this._calculateStartIndex() + this.state.itemCount
		const itemCount = this.props.items.length
		const renderPagination = itemCount > this.state.itemCount
		const renderItemCount = ITEM_PER_PAGE < itemCount
		const renderBottomPagination = itemsToRender.length >= 5

		const paginationComponent = (
			<div className="pagination-wrapper">
				{renderItemCount && (
					<ItemCountSelect value={this.state.itemCount} onChangeHandler={this._itemCountHandler} />
				)}
				{renderPagination && (
					<Fragment>
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
							previousLabel={<Icon size={11} kind="arrow-left" />}
							nextLabel={<Icon size={11} kind="arrow-right" />}
						/>
					</Fragment>
				)}
			</div>
		)

		return (
			<Fragment>
				{paginationComponent}
				{this.props.render(itemsToRender)}
				{renderBottomPagination && paginationComponent}
			</Fragment>
		)
	}

	_pageChangeHandler = e => {
		this.setState({
			currentPage: e.selected
		})
	}

	_itemCountHandler = e => {
		this.setState({ currentPage: 0, itemCount: e.value })
	}

	_calculatePageCount = () => {
		return Math.ceil(this.props.items.length / this.state.itemCount)
	}

	_calculateItemsToRender = () => {
		const startIndex = this._calculateStartIndex()
		return this.props.items.slice(startIndex, startIndex + this.state.itemCount)
	}

	_calculateStartIndex = () => {
		return this.state.currentPage * this.state.itemCount
	}
}
