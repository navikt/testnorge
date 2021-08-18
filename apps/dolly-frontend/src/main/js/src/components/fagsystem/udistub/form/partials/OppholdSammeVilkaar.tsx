import React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const OppholdSammeVilkaar = () => (
	<React.Fragment>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra"
			label="Oppholdstillatelse fra dato"
		/>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til"
			label="Oppholdstillatelse til dato"
		/>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering"
			label="Effektueringsdato"
		/>
		<FormikSelect
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType"
			label="Type oppholdstillatelse"
			options={Options('oppholdstillatelseType')}
		/>
		<FormikDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseVedtaksDato"
			label="Vedtaksdato"
		/>
	</React.Fragment>
)
