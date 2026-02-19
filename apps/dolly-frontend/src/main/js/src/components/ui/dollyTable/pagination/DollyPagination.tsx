import React, { Fragment, useEffect, useState } from 'react'
import ItemCountSelect from './ItemCountSelect/ItemCountSelect'

import './DollyPagination.less'
import { setSideStoerrelse, setSidetall } from '@/ducks/finnPerson'
import { useDispatch } from 'react-redux'
import { useLocation } from 'react-router'
import { Pagination } from '@navikt/ds-react'
import { sideStoerrelseLocalStorageKey } from '@/pages/gruppeOversikt/GruppeOversikt'

type PaginationProps = {
	visSide?: number
	gruppeDetaljer?: { pageSize: number; antallPages: number; antallElementer: number }
	items: any[]
	render: (arg0: any) => boolean | React.ReactChild
}

const ITEM_PER_PAGE = 10

export const DollyPagination = ({
	gruppeDetaljer: { antallElementer, antallPages, pageSize },
	items,
	render,
	visSide = 1,
	manualLocalStorageKey = null,
}: PaginationProps) => {
	const [currentPage, setCurrentPage] = useState(visSide)

	const currentPageSizeFromStorage = localStorage.getItem(
		manualLocalStorageKey ?? sideStoerrelseLocalStorageKey,
	)
	const [currentPageSize, setCurrentPageSize] = useState(
		currentPageSizeFromStorage ? Number(currentPageSizeFromStorage) : (pageSize ?? ITEM_PER_PAGE),
	)

	const location = useLocation()

	useEffect(() => {
		setCurrentPage(visSide)
	})

	const dispatch = useDispatch()

	const pageChangeHandler = (page: number) => {
		location.state = null
		dispatch(setSidetall(page - 1))
		setCurrentPage(page - 1)
	}

	const itemCountHandler = (event: { value: number }) => {
		dispatch(setSideStoerrelse(event.value))
		dispatch(setSidetall(0))
		setCurrentPage(0)
		setCurrentPageSize(event.value)
		localStorage.setItem(
			manualLocalStorageKey ?? sideStoerrelseLocalStorageKey,
			event.value?.toString(),
		)
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
		const startIdx = calculateStartIndex()
		return items.slice(startIdx, startIdx + currentPageSize)
	}

	const calculateStartIndex = () => {
		return currentPage * currentPageSize
	}

	if (!items) {
		return null
	}

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
					<Pagination
						style={{ marginTop: '5px' }}
						page={currentPage + 1}
						count={pageCount}
						size={'xsmall'}
						onPageChange={pageChangeHandler}
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
