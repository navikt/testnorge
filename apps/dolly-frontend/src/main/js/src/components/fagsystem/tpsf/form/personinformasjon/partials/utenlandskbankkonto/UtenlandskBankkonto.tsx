import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { AdresseKodeverk, ArbeidKodeverk } from '~/config/kodeverk'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'

const initialUtenlandskBankkonto = {
	kontonummerUtland: '',
	// @ts-ignore
	kontoRegdato: null,
	kodeSwift: '',
	kodeLand: '',
	bankNavn: '',
	iban: '',
	valuta: '',
	bankAdresse1: '',
	bankAdresse2: '',
	bankAdresse3: '',
}

export const UtenlandskBankkonto = () => {
	return (
		<Vis attributt={'tpsMessaging.utenlandskBankkonto'} formik>
			<FormikDollyFieldArray
				name="tpsMessaging.utenlandskBankkonto"
				header="Utenlandsk Bankkonto"
				newEntry={initialUtenlandskBankkonto}
				canBeEmpty={false}
			>
				{(path: string) => {
					return (
						<div className="flexbox--flex-wrap">
							<FormikTextInput name={`${path}.kontonummerUtland`} label={'Kontonummer'} />
							<FormikDatepicker name={`${path}.kontoRegdato`} label={'Registrert dato'} />
							<FormikTextInput name={`${path}.kodeSwift`} label={'Swift kode'} />
							<FormikSelect
								name={`${path}.kodeLand`}
								label={'Land'}
								kodeverk={AdresseKodeverk.ArbeidOgInntektLand}
								size={'xlarge'}
							/>
							<FormikTextInput name={`${path}.bankNavn`} label={'Banknavn'} />
							<FormikTextInput name={`${path}.iban`} label={'IBAN'} />
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
		</Vis>
	)
}
