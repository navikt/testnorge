import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { AdresseKodeverk } from '@/config/kodeverk'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { initialStatsborgerskap } from '@/components/fagsystem/pdlf/form/initialValues'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'

type StatsborgerskapTypes = {
	path: string
}

export const StatsborgerskapForm = ({ path }: StatsborgerskapTypes) => {
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
			<AvansertForm path={path} />
		</>
	)
}

export const Statsborgerskap = () => {
	return (
		<div className="flexbox--flex-wrap">
			<FormikDollyFieldArray
				name={'pdldata.person.statsborgerskap'}
				header="Statsborgerskap"
				newEntry={initialStatsborgerskap}
				canBeEmpty={false}
			>
				{(path: string, _idx: number) => <StatsborgerskapForm path={path} />}
			</FormikDollyFieldArray>
		</div>
	)
}
