import * as Yup from 'yup'
import { ifPresent, requiredDate, requiredNumber, requiredString } from '@/utils/YupValidations'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '@/service/SelectOptionsOppslag'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { EnheterForm } from './partials/enheterForm'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { useFormContext } from 'react-hook-form'

export const brregAttributt = 'brregstub'

export const BrregstubForm = () => {
	const formMethods = useFormContext()

	if (!formMethods.watch(brregAttributt)) {
		return null
	}

	const understatuser = SelectOptionsOppslag.hentUnderstatusFraBrregstub()
	const understatuserOptions = SelectOptionsFormat.formatOptions('understatuser', understatuser)
	console.log('understatuser: ', understatuser) //TODO - SLETT MEG

	return (
		<Vis attributt={brregAttributt}>
			<Panel
				heading="Brønnøysundregistrene"
				hasErrors={panelError(brregAttributt)}
				iconType="brreg"
				startOpen={erForsteEllerTest(formMethods.getValues(), [brregAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<FormSelect
						name="brregstub.understatuser"
						label="Understatuser"
						options={understatuserOptions}
						isLoading={understatuser.loading}
						isMulti={true}
						size="grow"
						isClearable={false}
					/>
					<EnheterForm formMethods={formMethods} />
				</div>
			</Panel>
		</Vis>
	)
}

BrregstubForm.validation = {
	brregstub: ifPresent(
		'$brregstub',
		Yup.object({
			understatuser: Yup.array().of(Yup.number()).required('Velg minst én understatus'),
			enheter: Yup.array().of(
				Yup.object({
					rolle: requiredString,
					registreringsdato: requiredDate,
					orgNr: requiredNumber,
					personroller: Yup.array().of(
						Yup.object({
							egenskap: requiredString,
							fratraadt: Yup.boolean(),
							registreringsdato: Yup.date(),
						}),
					),
				}),
			),
		}),
	),
}
