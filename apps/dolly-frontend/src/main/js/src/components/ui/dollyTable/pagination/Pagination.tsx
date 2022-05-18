import React, { Fragment, useEffect, useState } from 'react'
import ReactPaginate from 'react-paginate'
import Icon from '~/components/ui/icon/Icon'
import ItemCountSelect from './ItemCountSelect/ItemCountSelect'

import './Pagination.less'
import { setSideStoerrelse, setSidetall } from '~/ducks/finnPerson'
import { useDispatch } from 'react-redux'

type PaginationProps = {
	visSide?: number
	gruppeDetaljer?: { pageSize: number; antallPages: number; antallElementer: number }
	items: any[]
	render: (arg0: any) => boolean | React.ReactChild | React.ReactFragment | React.ReactPortal
}
const ITEM_PER_PAGE = 10

export const Pagination = ({
	gruppeDetaljer: { antallElementer, antallPages, pageSize },
	items,
	render,
	visSide = 0,
}: PaginationProps) => {
	const [currentPage, setCurrentPage] = useState(visSide)
	const [currentPageSize, setCurrentPageSize] = useState(pageSize || ITEM_PER_PAGE)

	useEffect(() => setCurrentPage(visSide))

	const dispatch = useDispatch()

	const pageChangeHandler = (event: { selected: number }) => {
		dispatch(setSidetall(event.selected))
		setCurrentPage(event.selected)
	}

	const itemCountHandler = (event: { value: number }) => {
		dispatch(setSideStoerrelse(event.value))
		dispatch(setSidetall(0))
		setCurrentPage(0)
		setCurrentPageSize(event.value)
	}

	const calculatePageCount = () => {
		if (antallPages) {
			return antallPages
		}
		const antall = antallElementer || items.length
		return Math.ceil(antall / currentPageSize)
	}

	const calculateItemsToRender = () => {
		if (antallElementer) {
			return items.slice(0, currentPageSize)
		}
		const startIndex = calculateStartIndex()
		return items.slice(startIndex, startIndex + currentPageSize)
	}

	const calculateStartIndex = () => {
		return currentPage * currentPageSize
	}

	if (!items) return null

	const pageCount = calculatePageCount()
	const itemsToRender = calculateItemsToRender()

	const startIndex = calculateStartIndex() + 1
	const lastIndex = calculateStartIndex() + currentPageSize
	const itemCount = antallElementer || items.length
	const renderPagination = itemCount > currentPageSize

	const paginationComponent = (
		<div className="pagination-wrapper">
			<ItemCountSelect value={currentPageSize} onChangeHandler={itemCountHandler} />
			{renderPagination && (
				<Fragment>
					<span className="pagination-label">
						Viser {startIndex}-{lastIndex > itemCount ? itemCount : lastIndex} av {itemCount}
					</span>
					<ReactPaginate
						containerClassName="pagination-container"
						forcePage={currentPage}
						pageCount={pageCount}
						pageRangeDisplayed={2}
						marginPagesDisplayed={1}
						onPageChange={pageChangeHandler}
						previousLabel={<Icon size={11} kind="arrow-left" />}
						nextLabel={<Icon size={11} kind="arrow-right" />}
					/>
				</Fragment>
			)}
		</div>
	)

	return (
		<Fragment>
			{render(itemsToRender)}
			{paginationComponent}
		</Fragment>
	)
}
