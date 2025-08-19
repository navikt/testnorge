import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { Option } from '@/service/SelectOptionsOppslag'
import React from 'react'
import { UseFormReturn } from 'react-hook-form/dist/types'

const pdlBasePath = 'pdldata.person.opphold'

export const OppholdSammeVilkaar = ({ formMethods }: { formMethods: UseFormReturn }) => (
	<React.Fragment>
		<FormDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.fra"
			duplicateName={`${pdlBasePath}[0].oppholdFra`}
			label="Oppholdstillatelse fra dato"
		/>
		<FormDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarPeriode.til"
			duplicateName={`${pdlBasePath}[0].oppholdTil`}
			label="Oppholdstillatelse til dato"
		/>
		<FormDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdSammeVilkaarEffektuering"
			label="Effektueringsdato"
		/>
		<FormSelect
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseType"
			label="Type oppholdstillatelse"
			afterChange={(option: Option) =>
				formMethods.setValue(`${pdlBasePath}[0].type`, option ? option.value : 'OPPLYSNING_MANGLER')
			}
			options={Options('oppholdstillatelseType')}
		/>
		<FormDatepicker
			name="udistub.oppholdStatus.oppholdSammeVilkaar.oppholdstillatelseVedtaksDato"
			label="Vedtaksdato"
		/>
	</React.Fragment>
)
