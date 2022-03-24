import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikProps } from 'formik'
import { Option } from '~/service/SelectOptionsOppslag'

const pdlBasePath = 'pdldata.person.opphold'

export const OppholdSammeVilkaar = ({ formikBag }: { formikBag: FormikProps<any> }) => (
	<React.Fragment>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra"
			afterChange={(dato: Date) => formikBag.setFieldValue(`${pdlBasePath}[0].oppholdFra`, dato)}
			label="Oppholdstillatelse fra dato"
		/>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til"
			afterChange={(dato: Date) => formikBag.setFieldValue(`${pdlBasePath}[0].oppholdTil`, dato)}
			label="Oppholdstillatelse til dato"
		/>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering"
			label="Effektueringsdato"
		/>
		<FormikSelect
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType"
			label="Type oppholdstillatelse"
			afterChange={(option: Option) =>
				formikBag.setFieldValue(
					`${pdlBasePath}[0].type`,
					option ? option.value : 'OPPLYSNING_MANGLER'
				)
			}
			options={Options('oppholdstillatelseType')}
		/>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseVedtaksDato"
			label="Vedtaksdato"
		/>
	</React.Fragment>
)
