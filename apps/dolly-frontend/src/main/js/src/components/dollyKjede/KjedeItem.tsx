import React from 'react'
import styled from 'styled-components'
import './DollyKjede.less'

export interface KjedeItemProps {
	index: number
	selected: boolean
	disabled: boolean
	text: string
	onClick: (page: number) => void
}

const Button = styled.button`
	background: transparent;
	text-decoration: ${p => p.style.textDecoration};
	cursor: ${p => p.style.cursor};
	border: none;
	font-size: 15px;
	padding: 0 9px 0 9px;
`

const disabledText = 'Lås opp lenkede måneder for å gjøre endringer på spesifikk måned'

export default ({ index, selected, disabled, text, onClick }: KjedeItemProps) => {
	const textColor = () => {
		if (selected) {
			return 'color-black'
		} else return disabled ? 'color-grey' : 'color-lightblue'
	}

	const style = {
		cursor: selected || disabled ? 'auto' : 'pointer',
		className: textColor(),
		textDecoration: selected ? 'none' : 'underline'
	}

	return (
		<Button
			disabled={disabled}
			onClick={() => onClick(index)}
			style={style}
			className={style['className']}
			title={disabled ? disabledText : null}
		>
			{text}
		</Button>
	)
}
