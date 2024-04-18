import { AdresseKodeverk } from '@/config/kodeverk'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { initialUtenlandsopphold } from '../initialValues'

const infotekst =
	'Start- og sluttdato må både være innenfor samme kalendermåned i samme år og perioden til arbeidsforholdet'

export const UtenlandsoppholdForm = ({ path }) => {
	return (
		<FormDollyFieldArray
			name={path}
			header="Utenlandsopphold"
			hjelpetekst={infotekst}
			newEntry={initialUtenlandsopphold}
			nested
		>
			{(partialPath, idx) => (
				<div key={idx} className="flexbox">
					<FormSelect
						name={`${partialPath}.land`}
						label="Land"
						kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
						isClearable={false}
						size="large"
					/>
					<FormDatepicker name={`${partialPath}.periode.fom`} label="Opphold fra" />
					<FormDatepicker name={`${partialPath}.periode.tom`} label="Opphold til" />
				</div>
			)}
		</FormDollyFieldArray>
	)
}
