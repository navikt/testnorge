import React, { Component, Fragment } from 'react'
import ReactPaginate from 'react-paginate'
import Icon from '~/components/ui/icon/Icon'
import ItemCountSelect from './ItemCountSelect/ItemCountSelect'

import './Pagination.less'

const ITEM_PER_PAGE = 10

export default class Pagination extends Component {
	state = {
		currentPage: this.props.visSide ? this.props.visSide : 0,
		pageSize: this.props.gruppeDetaljer?.pageSize
			? this.props.gruppeDetaljer.pageSize
			: ITEM_PER_PAGE
	}

	render() {
		if (!this.props.items) return null

		const pageCount = this._calculatePageCount()
		const itemsToRender = this._calculateItemsToRender()

		const startIndex = this._calculateStartIndex() + 1
		const lastIndex = this._calculateStartIndex() + this.state.pageSize
		const itemCount = this.props.gruppeDetaljer?.antallElementer
			? this.props.gruppeDetaljer.antallElementer
			: this.props.items.length
		const renderPagination = itemCount > this.state.pageSize

		const paginationComponent = (
			<div className="pagination-wrapper">
				<ItemCountSelect value={this.state.pageSize} onChangeHandler={this._itemCountHandler} />
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
				{this.props.render(itemsToRender)}
				{paginationComponent}
			</Fragment>
		)
	}

	_pageChangeHandler = e => {
		if (this.props.setSidetall) this.props.setSidetall(e.selected)
		this.setState({
			currentPage: e.selected
		})
	}

	_itemCountHandler = e => {
		if (this.props.setSidetall) this.props.setSidetall(0)
		if (this.props.setSideStoerrelse) this.props.setSideStoerrelse(e.value)
		this.setState({ currentPage: 0, pageSize: e.value })
	}

	_calculatePageCount = () => {
		if (this.props.gruppeDetaljer?.antallPages) {
			return this.props.gruppeDetaljer.antallPages
		}
		const antallElementer = this.props.gruppeDetaljer?.antallElementer
			? this.props.gruppeDetaljer.antallElementer
			: this.props.items.length
		return Math.ceil(antallElementer / this.state.pageSize)
	}

	_calculateItemsToRender = () => {
		if (this.props.gruppeDetaljer?.antallElementer) {
			return this.props.items.slice(0, this.state.pageSize)
		}
		const startIndex = this._calculateStartIndex()
		return this.props.items.slice(startIndex, startIndex + this.state.pageSize)
	}

	_calculateStartIndex = () => {
		return this.state.currentPage * this.state.pageSize
	}
}
