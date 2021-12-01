import * as React from 'react'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { FormikDollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { FormikTextInput } from '~/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk, GtKodeverk } from '~/config/kodeverk'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'

const initialUtenlandskBankkonto = {
	kontonummer: '',
	// @ts-ignore
	swift: '',
	landkode: '',
	banknavn: '',
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
				maxEntries={1}
				maxReachedDescription={'Kun 1 bankkonto støttet foreløpig'}
			>
				{(path: string) => {
					return (
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
