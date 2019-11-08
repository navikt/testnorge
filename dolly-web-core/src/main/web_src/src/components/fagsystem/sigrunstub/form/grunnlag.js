import React from 'react'
import { Kategori } from '~/components/ui/form/kategori/Kategori'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

const grunnlagType = []
export const Grunnlag = ({ formikProps }) => {
	return (
		<React.Fragment>
			<FormikSelect
				name={`sigrunstub.${idx}.tjeneste`}
				label="Inntektssted"
				options={Options('tjeneste')}
			/>
			<FormikSelect
				name={`sigrunstub.${idx}.tjeneste`}
				label="Tjeneste"
				options={Options('tjeneste')}
			/>
			<FormikSelect
				name={`sigrunstub.${idx}.grunnlag[0].tekniskNavn`}
				label="Type inntekt"
				kodeverk={formikProps.values.sigrunstub[idx].tjeneste}
			/>
			<FormikTextInput name={`sigrunstub.${idx}.grunnlag[0].verdi`} label="Beløp" />
		</React.Fragment>
	)
}
