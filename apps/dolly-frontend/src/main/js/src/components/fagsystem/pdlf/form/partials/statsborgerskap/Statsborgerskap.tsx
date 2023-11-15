import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { getInitialStatsborgerskap } from '@/components/fagsystem/pdlf/form/initialValues'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'
import { useContext } from 'react'
import { BestillingsveilederContext } from '@/components/bestillingsveileder/BestillingsveilederContext'

type StatsborgerskapTypes = {
	path: string
	identtype?: string
}

export const StatsborgerskapForm = ({ path, identtype }: StatsborgerskapTypes) => {
	const opts = useContext(BestillingsveilederContext)

	return (
		<>
			<FormikSelect
				name={`${path}.landkode`}
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				size="large"
				isClearable={false}
			/>
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.gyldigFraOgMed`}
					label="Statsborgerskap fra"
					maxDate={new Date()}
				/>
				<FormikDatepicker name={`${path}.gyldigTilOgMed`} label="Statsborgerskap til" />
			</DatepickerWrapper>
			<AvansertForm
				path={path}
				kanVelgeMaster={opts?.identtype !== 'NPID' && identtype !== 'NPID'}
			/>
		</>
	)
}

export const Statsborgerskap = () => {
	const opts = useContext(BestillingsveilederContext)

	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.statsborgerskap'}
				header="Statsborgerskap"
				newEntry={getInitialStatsborgerskap(opts?.identtype === 'NPID' ? 'PDL' : 'FREG')}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <StatsborgerskapForm path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
