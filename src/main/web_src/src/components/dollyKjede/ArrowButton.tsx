import React from 'react'
import styled from 'styled-components'
import Icon from '~/components/ui/icon/Icon'

export interface Props {
	left: boolean
	disabled: boolean
	onClick: (page: number) => void
}

const ArrowButton = styled.button`
	background: transparent;
	cursor: ${p => (p.disabled ? 'auto' : 'pointer')};
	border: none;
	fill: ${p => (p.disabled ? 'grey' : 'black')};
`

export default ({ left, disabled, onClick }: Props) => {
	const paginationValue = left ? -1 : 1

	return (
		<ArrowButton disabled={disabled} onClick={() => onClick(paginationValue)}>
			<Icon size={14} kind={left ? 'arrow-left' : 'arrow-right'} />
		</ArrowButton>
	)
}
