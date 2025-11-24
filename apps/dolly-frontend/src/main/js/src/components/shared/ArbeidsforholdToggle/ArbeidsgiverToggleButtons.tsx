import React from 'react'
import { ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import { ArbeidsgiverTyper } from '@/components/fagsystem/aareg/AaregTypes'
import { arbeidsgiverToggleValues } from '@/utils/OrgUtils'

const ToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
	background-color: #ffffff;
`

const DisabledToggleArbeidsgiver = styled(ToggleGroup)`
	display: grid;
	grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));

	:hover {
		background-color: white;
		cursor: default;
	}

	&&& {
		button {
			color: #aab0ba;
		}

		.navds-toggle-group__button[aria-checked='true'] {
			background-color: #aab0ba;
			color: white;

			:hover {
				background-color: #aab0ba;
				cursor: default;
			}
		}
	}
`

type ArbeidsgiverToggleButtonsProps = {
	value: ArbeidsgiverTyper
	onChange: (value: ArbeidsgiverTyper) => void
	isDisabled: boolean
	disablePrivat: boolean
	idx?: number
	path: string
}

export const ArbeidsgiverToggleButtons = ({
	value,
	onChange,
	isDisabled,
	disablePrivat,
	idx,
	path,
}: ArbeidsgiverToggleButtonsProps) => {
	const renderItems = () =>
		arbeidsgiverToggleValues
			.filter((t) => !(disablePrivat && t.value === ArbeidsgiverTyper.privat))
			.map((t) => (
				<ToggleGroup.Item key={t.value} value={t.value}>
					{t.label}
				</ToggleGroup.Item>
			))

	if (isDisabled) {
		return (
			<DisabledToggleArbeidsgiver
				onChange={() => null}
				value={value}
				size={'small'}
				fill
				key={idx ?? `${path}-disabled`}
				title={'Kan ikke endre arbeidsgivertype på eksisterende arbeidsforhold'}
			>
				{renderItems()}
			</DisabledToggleArbeidsgiver>
		)
	}

	return (
		<ToggleArbeidsgiver
			onChange={(value: string) => onChange(value as ArbeidsgiverTyper)}
			value={value}
			size={'small'}
			fill
			key={idx ?? `${path}-enabled`}
		>
			{renderItems()}
		</ToggleArbeidsgiver>
	)
}
