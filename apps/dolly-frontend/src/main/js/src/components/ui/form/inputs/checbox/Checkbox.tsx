import React, { forwardRef } from 'react'
import { Checkbox as DsCheckbox, Switch } from '@navikt/ds-react'
import styled from 'styled-components'
import { useFormContext } from 'react-hook-form'
import { InputWrapper } from '@/components/ui/form/inputWrapper/InputWrapper'

import './Checkbox.less'

const AttributeCheckbox = styled(DsCheckbox)`
	&&& .skjemaelement__label {
		text-transform: none;
	}
`
const SmallCheckbox = styled(DsCheckbox)`
	&&& .skjemaelement__label {
		font-size: 0.75em;
	}
`
const SpacedSwitch = styled(Switch)`
	&&& {
		margin-left: 3px;
	}
`

export type CheckboxSize = 'small' | 'medium'
export interface BaseCheckboxProps
	extends Omit<React.ComponentProps<typeof DsCheckbox>, 'children'> {
	label: React.ReactNode
	id?: string
	attributtCheckbox?: boolean
	compact?: boolean
	size?: CheckboxSize
}

export const Checkbox = forwardRef<HTMLInputElement, BaseCheckboxProps>(
	({ label, id, name, attributtCheckbox, compact, size = 'small', ...rest }, ref) => {
		const Comp = attributtCheckbox ? AttributeCheckbox : compact ? SmallCheckbox : DsCheckbox
		return (
			<Comp
				ref={ref}
				id={id || (name as string) || undefined}
				size={size === 'small' ? 'small' : 'medium'}
				{...rest}
			>
				{label}
			</Comp>
		)
	},
)
Checkbox.displayName = 'Checkbox'

export interface DollyCheckboxProps extends BaseCheckboxProps {
	isSwitch?: boolean
	disabled?: boolean
	wrapperSize?: 'grow' | 'shrink'
	checkboxMargin?: boolean
	vis?: boolean
}
export const DollyCheckbox: React.FC<DollyCheckboxProps> = ({
	isSwitch = false,
	disabled,
	wrapperSize = 'grow',
	checkboxMargin = false,
	vis = true,
	label,
	...rest
}) => {
	if (!vis) return null
	return (
		<InputWrapper size={wrapperSize} checkboxMargin={checkboxMargin}>
			{isSwitch ? (
				<SpacedSwitch disabled={disabled} {...rest}>
					{label}
				</SpacedSwitch>
			) : (
				<Checkbox disabled={disabled} label={label} {...rest} />
			)}
		</InputWrapper>
	)
}

export interface FormCheckboxProps
	extends Omit<DollyCheckboxProps, 'checked' | 'defaultChecked' | 'onChange' | 'name'> {
	name: string
	afterChange?: (checked: boolean) => void
}
export const FormCheckbox: React.FC<FormCheckboxProps> = ({
	name,
	afterChange,
	vis = true,
	...rest
}) => {
	const { watch, setValue, trigger } = useFormContext()
	const checked = !!watch(name)

	const handleChange: React.ChangeEventHandler<HTMLInputElement> = (e) => {
		const next = e.target.checked
		setValue(name, next, { shouldDirty: true })
		trigger(name)
		afterChange?.(next)
	}

	if (!vis) return null
	return <DollyCheckbox {...rest} name={name} vis checked={checked} onChange={handleChange} />
}
