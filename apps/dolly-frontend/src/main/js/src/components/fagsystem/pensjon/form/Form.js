import React from 'react'

import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import Panel from '~/components/ui/panel/Panel'
import { panelError, erForste } from '~/components/ui/form/formUtils'
import { validation } from '~/components/fagsystem/pensjon/form/validation'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import Formatters from '~/utils/DataFormatter'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { FormikCheckbox } from '~/components/ui/form/inputs/checbox/Checkbox'

const pensjonAttributt = 'pensjonforvalter'
const path = `${pensjonAttributt}.inntekt`

const hjelpetekst =
	'Hvis nedjuster med grunnbeløp er valgt skal beløp angis som årsbeløp i dagens kroneverdi, ' +
	'og vil nedjusteres basert på snitt grunnbeløp i inntektsåret.'

export const PensjonForm = ({ formikBag }) => (
	<Vis attributt={pensjonAttributt}>
		<Panel
			heading="Pensjonsgivende inntekt (POPP)"
			hasErrors={panelError(formikBag, pensjonAttributt)}
			iconType="pensjon"
			startOpen={() => erForste(formikBag.values, [pensjonAttributt])}
			informasjonstekst={hjelpetekst}
		>
			<Kategori title="Pensjonsgivende inntekt" vis={path}>
				<React.Fragment>
					<FormikSelect
						name={`${path}.fomAar`}
						label="Fra og med år"
						options={Formatters.getYearRangeOptions(1968, new Date().getFullYear() - 1)}
						isClearable={false}
					/>

					<FormikSelect
						name={`${path}.tomAar`}
						label="Til og med år"
						options={Formatters.getYearRangeOptions(1968, new Date().getFullYear() - 1)}
						isClearable={false}
					/>

					<FormikTextInput name={`${path}.belop`} label="Beløp" type="number" fastfield={false} />

					<FormikCheckbox
						name={`${path}.redusertMedGrunnbelop`}
						label="Nedjuster med grunnbeløp"
						checkboxMargin
						size="medium"
					/>
				</React.Fragment>
			</Kategori>
		</Panel>
	</Vis>
)

PensjonForm.validation = validation
