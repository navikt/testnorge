import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikProps } from 'formik'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk, ArbeidKodeverk } from '~/config/kodeverk'

interface utenlandskBankkonto {
	formikBag: FormikProps<{}>
}

const initialUtenlandskBankkonto = {
	giroNrUtland: '',
	kodeSwift: '',
	kodeLand: '',
	bankNavn: '',
	valuta: '',
	bankAdresse1: '',
	bankAdresse2: '',
	bankAdresse3: '',
}

export const UtenlandskBankkonto = ({ formikBag }: utenlandskBankkonto) => {
	return (
		<FormikDollyFieldArray
			name="tpsMessaging.utenlandskBankkonto"
			header="Utenlandsk Bankkonto"
			newEntry={initialUtenlandskBankkonto}
			canBeEmpty={false}
		>
			{(path: string) => {
				return (
					<div className="flexbox--flex-wrap">
						<FormikTextInput name={`${path}.giroNrUtland`} label={'Giro nummer'} />
						<FormikTextInput name={`${path}.kodeSwift`} label={'Swift kode'} />
						<FormikSelect
							name={`${path}.kodeLand`}
							label={'Land'}
							kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
							size={'xlarge'}
						/>
						<FormikTextInput name={`${path}.bankNavn`} label={'Banknavn'} />
						<FormikSelect
							name={`${path}.valuta`}
							label="Valuta"
							kodeverk={ArbeidKodeverk.Valutaer}
							size={'xlarge'}
						/>
						<FormikTextInput name={`${path}.bankAdresse1`} label={'Bank Adresse 1'} />
						<FormikTextInput name={`${path}.bankAdresse2`} label={'Bank Adresse 2'} />
						<FormikTextInput name={`${path}.bankAdresse3`} label={'Bank Adresse 3'} />
					</div>
				)
			}}
		</FormikDollyFieldArray>
	)
}
