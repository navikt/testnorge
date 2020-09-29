import React from 'react'
import * as Yup from 'yup'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import Panel from '~/components/ui/panel/Panel'
import { erForste } from '~/components/ui/form/formUtils'
import { panelError } from '~/components/ui/form/formUtils'

const krrAttributt = 'krrstub'

export const KrrstubForm = ({ formikBag }) => (
	<Vis attributt={krrAttributt}>
		<Panel
			heading="Kontakt- og reservasjonsregisteret"
			hasErrors={panelError(formikBag, krrAttributt)}
			iconType="krr"
			startOpen={() => erForste(formikBag.values, [krrAttributt])}
		>
			<div className="flexbox--flex-wrap">
				<FormikTextInput name="krrstub.epost" label="E-post" />
				<FormikTextInput name="krrstub.mobil" label="Mobilnummer" type="number" />
				<FormikTextInput name="krrstub.sdpAdresse" label="SDP Adresse" />
				<FormikTextInput name="krrstub.sdpLeverandoer" label="SDP leverandør" type="number" />
				<FormikSelect name="krrstub.spraak" label="Språk" options={Options('spraaktype')} />
				<FormikSelect
					name="krrstub.registrert"
					label="Registrert i DKIF"
					options={Options('boolean')}
				/>
				<FormikSelect name="krrstub.reservert" label="Reservert" options={Options('boolean')} />
				<FormikDatepicker name="krrstub.gyldigFra" label="Kontaktinfo gjelder fra" />
			</div>
		</Panel>
	</Vis>
)

KrrstubForm.validation = {
	krrstub: Yup.object({
		epost: '',
		gyldigFra: Yup.date().nullable(),
		mobil: Yup.string().matches(/^[0-9]*$/, 'Ugyldig mobilnummer'),
		sdpAdresse: '',
		sdpLeverandoer: '',
		spraak: '',
		registrert: '',
		reservert: ''
	})
}
