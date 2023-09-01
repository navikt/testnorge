import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormikSelect } from '@/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kontakt } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/Kontakt'
import { Adresse } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/Adresse'
import { FormikDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialKontaktinfoForDoedebo } from '@/components/fagsystem/pdlf/form/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import React from 'react'
import { DatepickerWrapper } from '@/components/ui/form/inputs/datepicker/DatepickerStyled'

export const doedsboAttributt = 'pdldata.person.kontaktinformasjonForDoedsbo'

export const KontaktinformasjonForDoedsboForm = ({
	formikBag,
	path,
	eksisterendeNyPerson = null,
}) => {
	return (
		<>
			<FormikSelect
				name={`${path}.skifteform`}
				label="Skifteform"
				options={Options('skifteform')}
				isClearable={false}
			/>
			<DatepickerWrapper>
				<FormikDatepicker
					name={`${path}.attestutstedelsesdato`}
					label="Utstedelsesdato skifteattest"
				/>
			</DatepickerWrapper>
			<Kontakt formikBag={formikBag} path={path} eksisterendeNyPerson={eksisterendeNyPerson} />
			<Adresse formikBag={formikBag} path={`${path}.adresse`} />
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const KontaktinformasjonForDoedsbo = ({ formikBag }) => {
	return (
		<Vis attributt={doedsboAttributt}>
			<Panel
				heading="Kontaktinformasjon for dødsbo"
				hasErrors={panelError(formikBag, doedsboAttributt)}
				iconType="designsystem-doedsbo"
				startOpen={erForsteEllerTest(formikBag.values, [doedsboAttributt])}
			>
				<FormikDollyFieldArray
					name="pdldata.person.kontaktinformasjonForDoedsbo"
					header="Kontaktinformasjon for dødsbo"
					newEntry={initialKontaktinfoForDoedebo}
					canBeEmpty={false}
				>
					{(path) => <KontaktinformasjonForDoedsboForm formikBag={formikBag} path={path} />}
				</FormikDollyFieldArray>
			</Panel>
		</Vis>
	)
}
