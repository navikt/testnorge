import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'

type NomFormProps = {
	formikBag: FormikProps<{}>
}

const nomAttributt = 'nomData'

export const NomForm = ({ formikBag }: NomFormProps) => (
	<Vis attributt={nomAttributt}>
		<Panel
			heading="NAV ansatt"
			iconType="organisasjon"
			//@ts-ignore
			startOpen={() => erForste(formikBag.values, [nomAttributt])}
		>
			<div className="flexbox--flex-wrap">
				<FormikSelect
					name="nomData.opprettNavIdent"
					label="Har nav ident"
					options={Options('boolean')}
				/>
			</div>
		</Panel>
	</Vis>
)
