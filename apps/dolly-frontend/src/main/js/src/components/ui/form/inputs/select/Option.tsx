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

			&.selected {
				background-color: #2684ffff;
				color: #ffffff;

				&:hover {
					background-color: #2684ffff;
				}
			}
			&.disabled.main {
				color: grey;
				background-color: #f2f2f2;
				&:hover {
					cursor: not-allowed;
					background-color: #f2f2f2;
				}
			}
			&.disabled {
				color: grey;
				&:hover {
					cursor: not-allowed;
				}
			}
			&.main {
				background-color: #f2f2f2;
				&:hover {
					background-color: #cde1f3;
				}
			}
			&.indented {
				padding-left: 30px;
			}
		}
	}
`

const Option = ({ children, isSelected, isDisabled, label, innerProps }) => (
	<StyledOption
		className={cx('dolly-select__option', {
			selected: isSelected,
			disabled: isDisabled,
			indented: label.startsWith('   '),
			main: label.endsWith('   '),
		})}
		id={innerProps.id}
		tabIndex={innerProps.tabIndex}
		onClick={innerProps.onClick}
	>
		{children}
	</StyledOption>
)

export default Option
