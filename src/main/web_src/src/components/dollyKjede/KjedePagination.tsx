import React from 'react'
import styled from 'styled-components'
import KjedeItem from '~/components/dollyKjede/KjedeItem'
import ArrowButton from '~/components/dollyKjede/ArrowButton'
import './DollyKjede.less'

export interface Props {
	selectedIndex: number
	objectList: string[]
	centerIndices: number[]
	disabled: boolean
	handlePagination: (page: number) => void
	handleClick: (page: number) => void
}

const PaginationContainer = styled.div`
	border-radius: 2px;
	margin: 2px;
	padding: 9px 7px 7px 7px;
	width: 800px;
`

const PaginationWrapper = styled.div`
	display: flex;
	justify-content: center;
`

const Separator = styled.div`
	font-weight: bold;
	margin: 0 20px 0 20px;
	color: ${props => props.color};
`

export default ({
	selectedIndex,
	objectList,
	centerIndices,
	disabled,
	handlePagination,
	handleClick
}: Props) => {
	return (
		<PaginationContainer className={'background-color-bg-divider'}>
			<PaginationWrapper>
				<ArrowButton
					left={true}
					disabled={disabled || centerIndices.length == 0 || centerIndices[0] == 1}
					onClick={handlePagination}
				/>
				{objectList.length > 0 && (
					<KjedeItem
						index={0}
						selected={selectedIndex === 0}
						disabled={disabled}
						text={objectList[0]}
						onClick={handleClick}
					/>
				)}
				{centerIndices.length != 0 && centerIndices[0] != 1 && (
					<Separator color={disabled ? 'grey' : 'black'}>...</Separator>
				)}
				{centerIndices.map((item, index) => {
					return (
						<KjedeItem
							key={index}
							index={item}
							selected={selectedIndex == item}
							disabled={disabled}
							text={objectList[item]}
							onClick={handleClick}
						/>
					)
				})}
				{centerIndices.length != 0 &&
					centerIndices[centerIndices.length - 1] != objectList.length - 2 && (
						<Separator color={disabled ? 'grey' : 'black'}>...</Separator>
					)}
				{objectList.length > 1 && (
					<KjedeItem
						index={objectList.length - 1}
						selected={selectedIndex == objectList.length - 1}
						disabled={disabled}
						text={objectList[objectList.length - 1]}
						onClick={handleClick}
					/>
				)}
				<ArrowButton
					left={false}
					disabled={
						disabled ||
						centerIndices.length == 0 ||
						centerIndices[centerIndices.length - 1] == objectList.length - 2
					}
					onClick={handlePagination}
				/>
			</PaginationWrapper>
		</PaginationContainer>
	)
}
