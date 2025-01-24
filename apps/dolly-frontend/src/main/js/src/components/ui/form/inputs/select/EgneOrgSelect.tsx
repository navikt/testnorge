import cx from 'classnames'
import cn from 'classnames'
import styled from 'styled-components'
import { createFilter, default as ReactSelect } from 'react-select'
import MenuList from '@/components/ui/form/inputs/select/MenuList'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'
import { Label } from '@/components/ui/form/inputs/label/Label'
import './Select.less'

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

				&.selected {
					background-color: #2684ffff;
				}

				&.disabled {
					cursor: not-allowed;
					background-color: #f2f2f2;
				}

				&.main {
					background-color: #cde1f3;
				}
			}

			&.selected {
				background-color: #2684ffff;
				color: #ffffff;

				&.main {
					background-color: #2684ffff;
				}
			}

			&.disabled {
				color: grey;
				background-color: #f2f2f2;
			}

			&.main {
				background-color: #eaf3fa;

				&.disabled {
					background-color: #f2f2f2;
				}
			}

			&.indented {
				padding-left: 30px;
			}
		}
	}
`

type OptionProps = {
	children?: any
	isSelected?: boolean
	isDisabled?: boolean
	label: string
	innerProps: any
}

const Option = ({ children, isSelected, isDisabled, label, innerProps }: OptionProps) => (
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

export const EgneOrgSelect = (props: any) => {
	return (
		<InputWrapper {...props}>
			<Label
				containerClass="dollyselect"
				name={props.name}
				label={props.label}
				feil={props.feil}
				info={props.info}
			>
				<ReactSelect
					value={props.options.filter((o: any) => o.value === props.value)}
					options={props.options}
					name={props.name}
					filterOption={createFilter({ ignoreAccents: false })}
					onChange={props.onChange}
					isLoading={props.isLoading}
					placeholder={'Velg organisasjon ...'}
					className={cn('basic-single', props.className)}
					classNamePrefix={'select'}
					components={{
						MenuList,
						Option,
					}}
					isClearable={props.isClearable}
					styles={{ menuPortal: (base) => ({ ...base, zIndex: 99999 }) }}
					menuPortalTarget={document.getElementById('root')}
					isDisabled={props.isDisabled}
				/>
			</Label>
		</InputWrapper>
	)
}
