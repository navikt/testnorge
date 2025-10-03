import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { Mal } from '@/utils/hooks/useMaler'
import { Switch, ToggleGroup } from '@navikt/ds-react'
import styled from 'styled-components'
import {
	getMalOptions,
	MalTyper,
	toggleMalValues,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/MalForm'
import React from 'react'
import { TestComponentSelectors } from '#/mocks/Selectors'

type Props = {
	onChange: (value: React.BaseSyntheticEvent) => void
	onToggleChange: (value: MalTyper) => void
	opprettMal: boolean
	typeMal: MalTyper
	malbestillinger: Mal[]
}

const StyledToggleGroup = styled(ToggleGroup)`
	margin-bottom: 10px;
`

const Tittel = styled.div`
	display: flex;
	flex-wrap: wrap;
	margin-bottom: 10px;

	h2 {
		font-size: 1.4rem;
	}
`

export const MalOppsummering = ({
	malbestillinger,
	onChange,
	onToggleChange,
	opprettMal,
	typeMal,
}: Props) => (
	<div className="input-oppsummering">
		<Tittel>
			<h2>Legg til mal</h2>
			<Switch
				data-testid={TestComponentSelectors.TOGGLE_BESTILLING_MAL}
				onChange={onChange}
				children={null}
			/>
		</Tittel>
		{opprettMal && (
			<span>
				<div className="flexbox--align-center">
					<div className="toggle--wrapper">
						<StyledToggleGroup
							size={'small'}
							onChange={onToggleChange}
							defaultValue={MalTyper.OPPRETT}
						>
							{toggleMalValues.map((type) => (
								<ToggleGroup.Item key={type.value} value={type.value}>
									{type.label}
								</ToggleGroup.Item>
							))}
						</StyledToggleGroup>
					</div>
				</div>
				{typeMal === MalTyper.ENDRE ? (
					<FormSelect
						name={'malBestillingNavn'}
						size={'xlarge'}
						label="Malnavn"
						options={getMalOptions(malbestillinger)}
						autoFocus
					/>
				) : (
					<FormTextInput
						useControlled
						data-testid={TestComponentSelectors.INPUT_BESTILLING_MALNAVN}
						name="malBestillingNavn"
						size={'xlarge'}
						label="Malnavn"
						autoFocus
					/>
				)}
			</span>
		)}
	</div>
)
