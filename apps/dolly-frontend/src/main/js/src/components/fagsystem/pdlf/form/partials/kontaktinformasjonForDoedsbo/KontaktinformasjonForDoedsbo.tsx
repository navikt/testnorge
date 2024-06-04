import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { Kontakt } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/Kontakt'
import { Adresse } from '@/components/fagsystem/pdlf/form/partials/kontaktinformasjonForDoedsbo/Adresse'
import { FormDollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { AvansertForm } from '@/components/fagsystem/pdlf/form/partials/avansert/AvansertForm'
import { initialKontaktinfoForDoedebo } from '@/components/fagsystem/pdlf/form/initialValues'
import Panel from '@/components/ui/panel/Panel'
import { erForsteEllerTest, panelError } from '@/components/ui/form/formUtils'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import React from 'react'

export const doedsboAttributt = 'pdldata.person.kontaktinformasjonForDoedsbo'

export const KontaktinformasjonForDoedsboForm = ({
	formMethods,
	path,
	eksisterendeNyPerson = null,
}) => {
	return (
		<>
			<FormSelect
				name={`${path}.skifteform`}
				label="Skifteform"
				options={Options('skifteform')}
				isClearable={false}
			/>
			<FormDatepicker name={`${path}.attestutstedelsesdato`} label="Utstedelsesdato skifteattest" />
			<Kontakt formMethods={formMethods} path={path} eksisterendeNyPerson={eksisterendeNyPerson} />
			<Adresse formMethods={formMethods} path={`${path}.adresse`} />
			<AvansertForm path={path} kanVelgeMaster={false} />
		</>
	)
}

export const KontaktinformasjonForDoedsbo = ({ formMethods }) => {
	return (
		<Vis attributt={doedsboAttributt}>
			<Panel
				heading="Kontaktinformasjon for dødsbo"
				hasErrors={panelError(doedsboAttributt)}
				iconType="doedsbo"
				startOpen={erForsteEllerTest(formMethods.getValues(), [doedsboAttributt])}
			>
				<FormDollyFieldArray
					name="pdldata.person.kontaktinformasjonForDoedsbo"
					header="Kontaktinformasjon for dødsbo"
					newEntry={initialKontaktinfoForDoedebo}
					canBeEmpty={false}
				>
					{(path) => <KontaktinformasjonForDoedsboForm formMethods={formMethods} path={path} />}
				</FormDollyFieldArray>
			</Panel>
		</Vis>
	)
}
