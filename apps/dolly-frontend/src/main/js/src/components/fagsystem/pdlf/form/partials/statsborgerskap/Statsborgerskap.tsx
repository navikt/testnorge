import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialStatsborgerskap } from '@/components/fagsystem/pdlf/form/initialValues'
import { useContext } from 'react'
import {
	BestillingsveilederContext,
	BestillingsveilederContextType,
} from '@/components/bestillingsveileder/BestillingsveilederContext'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import { useBoolean } from 'react-use'
import { UseFormReturn } from 'react-hook-form/dist/types'
import styled from 'styled-components'

type StatsborgerskapTypes = {
	path: string
	kanVelgeMaster?: boolean
	formMethods?: UseFormReturn
}

const LandVelger = styled.div`
	display: flex;
	margin-right: 25px;
`

export const StatsborgerskapForm = ({
	path,
	kanVelgeMaster,
	formMethods,
}: StatsborgerskapTypes) => {
	const [ukjentIsChecked, setUkjentIsChecked] = useBoolean(false)

	const handleUkjentLandChange = (isChecked: boolean) => {
		setUkjentIsChecked(isChecked)
		formMethods?.setValue(`${path}.landkode`, isChecked ? 'XUK' : null)
	}

	return (
		<>
			<LandVelger>
				<FormSelect
					name={`${path}.landkode`}
					label="Statsborgerskap"
					kodeverk={AdresseKodeverk.StatsborgerskapLand}
					size="large"
					isClearable={false}
					isDisabled={ukjentIsChecked}
				/>
				<FormCheckbox
					name={`${path}.ukjentLand`}
					label="Ukjent land"
					checkboxMargin
					wrapperSize="tight"
					afterChange={(val: boolean) => handleUkjentLandChange(val)}
				/>
			</LandVelger>
			<FormDatepicker
				name={`${path}.gyldigFraOgMed`}
				label="Statsborgerskap fra"
				maxDate={new Date()}
			/>
			<FormDatepicker name={`${path}.gyldigTilOgMed`} label="Statsborgerskap til" />
			<AvansertForm path={path} kanVelgeMaster={kanVelgeMaster} />
		</>
	)
}

export const Statsborgerskap = ({ formMethods }: { formMethods: UseFormReturn }) => {
	const opts = useContext(BestillingsveilederContext) as BestillingsveilederContextType

	return (
		<div className="flexbox--flex-wrap">
			<FormDollyFieldArray
				name={'pdldata.person.statsborgerskap'}
				header="Statsborgerskap"
				newEntry={getInitialStatsborgerskap(
					opts?.identMaster === 'PDL' || opts?.identtype === 'NPID' ? 'PDL' : 'FREG',
				)}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => (
					<StatsborgerskapForm
						path={path}
						kanVelgeMaster={opts?.identMaster !== 'PDL' && opts?.identtype !== 'NPID'}
						formMethods={formMethods}
					/>
				)}
			</FormDollyFieldArray>
		</div>
	)
}
