import React from 'react'
import cx from 'classnames'
import styled from 'styled-components'

const StyledOption = styled.div`
	&.dolly-select {
		&__option {
			display: block;
			font-size: 16px;
			color: rgb(36, 36, 36);
			padding: 11px 12px;
			width: 100%;
			user-select: none;
			box-sizing: border-box;

			&:hover {
				background-color: #cde1f3;
			}

			&_selected {
				background-color: #2684ffff;
				color: #ffffff;

				&:hover {
					background-color: #2684ffff;
				}
			}
		}
	}
`

const Option = ({ children, isSelected, innerProps }) => (
	<StyledOption
		className={cx('dolly-select__option', {
			'dolly-select__option_selected': isSelected
		})}
		id={innerProps.id}
		tabIndex={innerProps.tabIndex}
		onClick={innerProps.onClick}
	>
		{children}
	</StyledOption>
)

export default Option
