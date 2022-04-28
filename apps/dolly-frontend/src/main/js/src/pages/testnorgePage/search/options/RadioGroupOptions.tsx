import React from 'react'
import Icon from '~/components/ui/icon/Icon'
import { Radio, RadioGroup } from '@navikt/ds-react'
import { FormikProps } from 'formik'
import styled from 'styled-components'
import _get from 'lodash/get'

type Option = {
	value: string
	label: string
	disabled?: boolean
}

type RadioOptionsProps = {
	formikBag: FormikProps<{}>
	name: string
	path: string
	legend?: string
	hideLegend?: boolean
	options: Option[]
}

const IconContainer = styled.div`
	margin: 0 0 0 auto;
`

export const RadioGroupOptions = ({
	formikBag,
	name,
	path,
	legend = 'Velg',
	hideLegend = true,
	options,
}: RadioOptionsProps) => {
	const selected = _get(formikBag.values, `${path}`) || null
	const setSelected = (valg: string) => {
		formikBag.setFieldValue(`${path}`, valg)
	}
	return (
		<div className="radio-group">
			<div className="flexbox--align-center">
				<div className="options-title">{name}</div>
				{selected != null && (
					<IconContainer onClick={() => setSelected('')}>
						<Icon size={14} kind={'trashcan'} />
					</IconContainer>
				)}
			</div>
			<div className="radio-group__options">
				<RadioGroup value={selected} legend={legend} hideLegend={hideLegend} size="medium">
					{options.map((option, idx) => {
						return (
							<Radio
								key={idx}
								value={option.value}
								onChange={(event) => setSelected(event.target.value)}
								disabled={option.disabled}
							>
								{option.label}
							</Radio>
						)
					})}
				</RadioGroup>
			</div>
		</div>
	)
}
