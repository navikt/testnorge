import React from 'react'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { DollySelect, FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste } from '~/components/ui/form/formUtils'
import { FormikProps } from 'formik'
import { useBoolean } from 'react-use'

type NomFormProps = {
	formikBag: FormikProps<{}>
}

const nomAttributt = 'nomData'

export const NomForm = ({ formikBag }: NomFormProps) => {
	const [registrert, setRegistrert] = useBoolean(true)
	return (
		//@ts-ignore
		<Vis attributt={nomAttributt}>
			<Panel
				heading="NAV Organisasjonsmaster"
				iconType="organisasjon"
				//@ts-ignore
				startOpen={() => erForste(formikBag.values, [nomAttributt])}
			>
				<div className="flexbox--flex-wrap">
					<FormikSelect
						name="nomData.opprettNavIdent"
						label="Registrert i Nom"
						// value={registrert}
						// onChange={(event: { value: boolean }) => setRegistrert(event.value)}
						options={Options('boolean')}
					/>
				</div>
			</Panel>
		</Vis>
	)
}
