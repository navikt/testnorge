import Icon from '@/components/ui/icon/Icon'
import { Radio, RadioGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import * as _ from 'lodash'
import { UseFormReturn } from 'react-hook-form/dist/types'

type Option = {
	value: string
	label: string
	disabled?: boolean
}

type RadioOptionsProps = {
	formMethods: UseFormReturn
	name: string
	path: string
	legend?: string
	hideLegend?: boolean
	options: Option[]
}

const IconContainer = styled.div`
	margin: 0 0 0 auto;
	cursor: pointer;
`

export const RadioGroupOptions = ({
	formMethods,
	name,
	path,
	legend = 'Velg',
	hideLegend = true,
	options,
}: RadioOptionsProps) => {
	const selected = _.get(formMethods.getValues(), `${path}`) || null
	const setSelected = (valg: string) => {
		formMethods.setValue(`${path}`, valg)
	}
	return (
		<div className="radio-group">
			<div className="flexbox--align-center">
				<div className="options-title">{name}</div>
				{selected != null && (
					<IconContainer onClick={() => setSelected('')}>
						<Icon fontSize={'1.4rem'} kind={'trashcan'} style={{ color: '#0067C5' }} />
					</IconContainer>
				)}
			</div>
			<div className="radio-group__options">
				<RadioGroup value={selected} legend={legend} hideLegend={hideLegend} size="small">
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
