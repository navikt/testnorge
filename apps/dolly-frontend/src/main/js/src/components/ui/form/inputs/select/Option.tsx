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
				cursor: pointer;
			}

			&_selected {
				background-color: #2684ffff;
				color: #ffffff;

				&:hover {
					background-color: #2684ffff;
				}
			}

			&_disabled {
				color: grey;
				background-color: #f2f2f2;
				&:hover {
					cursor: not-allowed;
					background-color: #f2f2f2;
				}
			}
		}
	}
`

const Option = ({ children, isSelected, isDisabled, innerProps }) => (
	<StyledOption
		className={cx('dolly-select__option', {
			'dolly-select__option_selected': isSelected,
			'dolly-select__option_disabled': isDisabled,
		})}
		id={innerProps.id}
		tabIndex={innerProps.tabIndex}
		onClick={innerProps.onClick}
	>
		{children}
	</StyledOption>
)

export default Option
