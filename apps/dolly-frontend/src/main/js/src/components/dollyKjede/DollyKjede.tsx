import React, { useEffect, useState } from 'react'
import KjedePagination from '~/components/dollyKjede/KjedePagination'

export interface DollyKjedeProps {
	objectList: string[]
	itemLimit: number
	selectedIndex: number
	setSelectedIndex: (index: number) => void
	isLocked: boolean
}

const getCenterIndices = (index: number, antallItems: number, itemLimit: number) => {
	if (antallItems < 3) {
		return []
	}

	let indices = [index]

	if (antallItems <= itemLimit) {
		indices = Array.from(Array(antallItems).keys())
	}

	while (
		indices.length < itemLimit - 3 &&
		!indices.includes(1) &&
		!(indices.length == itemLimit - 4 && indices[indices.length - 1] != antallItems - 2)
	) {
		indices.unshift(indices[0] - 1)
	}

	while (
		indices.length < itemLimit - 3 &&
		!indices.includes(antallItems - 2) &&
		!(indices.length == itemLimit - 4 && indices[0] != 1)
	) {
		indices.push(indices[indices.length - 1] + 1)
	}

	if (indices.includes(0)) {
		indices = indices.slice(1)
	}

	if (indices.includes(antallItems - 1)) {
		indices = indices.slice(0, indices.length - 1)
	}

	return indices
}

const getMaxShownItems = (itemLimit: number) => {
	return itemLimit > 10 ? 10 : itemLimit < 5 ? 5 : itemLimit
}

const getInitialPaginationIndex = (antallItems: number, maxShownItems: number) => {
	return antallItems >= maxShownItems ? maxShownItems - 4 : antallItems - 2
}

export default ({
	objectList,
	itemLimit,
	selectedIndex,
	setSelectedIndex,
	isLocked,
}: DollyKjedeProps) => {
	useEffect(() => {
		setLocked(isLocked)
	}, [isLocked])

	const antallItems = objectList.length
	const maxShownItems = getMaxShownItems(itemLimit)

	const [locked, setLocked] = useState(isLocked)

	const [paginationIndex, setPaginationIndex] = useState(
		getInitialPaginationIndex(antallItems, maxShownItems)
	)
	const [centerIndices, setCenterIndices] = useState(
		getCenterIndices(paginationIndex, antallItems, maxShownItems)
	)

	useEffect(() => {
		setCenterIndices(getCenterIndices(paginationIndex, antallItems, maxShownItems))
	}, [antallItems])

	const handlePagination = (addValue: number) => {
		if (centerIndices.length == maxShownItems - 3) {
			if (addValue < 0) {
				addValue -= 1
			} else {
				addValue += 1
			}
		}
		setCenterIndices(getCenterIndices(paginationIndex + addValue, antallItems, maxShownItems))
		setPaginationIndex(paginationIndex + addValue)
	}

	const handleSelection = (index: number) => {
		let newPaginationIndex
		if (index == 0) {
			newPaginationIndex = getInitialPaginationIndex(antallItems, maxShownItems)
		} else if (index == antallItems - 1) {
			newPaginationIndex = antallItems - 2
		}
		if (newPaginationIndex != null) {
			setCenterIndices(getCenterIndices(newPaginationIndex, antallItems, maxShownItems))
			setPaginationIndex(newPaginationIndex)
		}
		setSelectedIndex(index)
	}

	return (
		<KjedePagination
			selectedIndex={selectedIndex}
			objectList={objectList}
			centerIndices={centerIndices}
			disabled={locked}
			handlePagination={handlePagination}
			handleClick={handleSelection}
		/>
	)
}
