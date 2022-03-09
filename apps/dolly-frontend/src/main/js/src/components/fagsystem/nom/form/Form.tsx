import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { DollySelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'

type NomFormProps = {
	formikBag: FormikProps<{}>
}

const nomAttributt = 'nom'

export const NomForm = ({ formikBag }: NomFormProps) => {
	return (
		//@ts-ignore
		<Vis attributt={nomAttributt}>
			<Panel
				heading="NAV Organisasjonsmaster"
				iconType="krr"
				//@ts-ignore
				startOpen={() => erForste(formikBag.values, [nomAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<DollySelect
						name="nom.opprettNavIdent"
						label="Registrert i Nom"
						options={Options('boolean')}
					/>
				</div>
			</Panel>
		</Vis>
	)
}
