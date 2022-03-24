import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk, GtKodeverk } from '~/config/kodeverk'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'

const path = 'tpsMessaging.utenlandskBankkonto'

export const UtenlandskBankkonto = () => (
	<Vis attributt={path} formik>
		<div className="flexbox--flex-wrap">
			<FormikTextInput name={`${path}.kontonummer`} label={'Kontonummer'} />
			<FormikTextInput name={`${path}.swift`} label={'Swift kode'} />
			<FormikSelect
				name={`${path}.landkode`}
				label={'Land'}
				kodeverk={GtKodeverk.LAND}
				size={'xlarge'}
			/>
			<FormikTextInput name={`${path}.banknavn`} label={'Banknavn'} />
			<FormikTextInput name={`${path}.iban`} label={'IBAN'} />
			<FormikSelect
				name={`${path}.valuta`}
				label="Valuta"
				kodeverk={ArbeidKodeverk.Valutaer}
				size={'xlarge'}
			/>
			<FormikTextInput name={`${path}.bankAdresse1`} label={'Adresselinje 1'} />
			<FormikTextInput name={`${path}.bankAdresse2`} label={'Adresselinje 2'} />
			<FormikTextInput name={`${path}.bankAdresse3`} label={'Adresselinje 3'} />
		</div>
	</Vis>
)
