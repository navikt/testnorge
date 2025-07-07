import * as React from 'react'
import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { FormTextInput } from '@/components/ui/form/inputs/textInput/TextInput'
import { ArbeidKodeverk, GtKodeverk } from '@/config/kodeverk'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'
import { FormCheckbox } from '@/components/ui/form/inputs/checbox/Checkbox'
import * as _ from 'lodash-es'
import { landkodeIsoMapping } from '@/service/services/kontoregister/landkoder'

const path = 'bankkonto.utenlandskBankkonto'

export const UtenlandskBankkonto = ({ formMethods }: any) => {
	const values = formMethods.getValues()
	const disableKontonummer = _.get(values, `${path}.tilfeldigKontonummer`)
	const disableTilfeldigKontonummer = _.get(values, `${path}.kontonummer`)

	const updateSwiftLandkode = (selected: any) => {
		if (!selected) {
			return
		}
		const landkode = selected.value
		// @ts-ignore
		const isoLandkode = landkodeIsoMapping[landkode]
		if (isoLandkode) {
			const mappedLandkode = isoLandkode || landkode.substring(0, 2)
			let swift = _.get(values, `${path}.swift`)
			if (swift) {
				swift = swift.substring(0, 4) + mappedLandkode + swift.substring(6)
			} else {
				swift = 'BANK' + mappedLandkode + '11222'
			}
			formMethods.setValue(`${path}.swift`, swift, false)
		}
	}

	return (
		<Vis attributt={path}>
			<div className="flexbox--flex-wrap">
				<div className="flexbox--flex-wrap">
					<FormTextInput
						name={`${path}.kontonummer`}
						label={'Kontonummer'}
						isDisabled={disableKontonummer}
					/>
					<div style={{ marginTop: '17px' }}>
						<FormCheckbox
							name={`${path}.tilfeldigKontonummer`}
							label="Har tilfeldig kontonummer"
							size="medium"
							isDisabled={disableTilfeldigKontonummer}
						/>
					</div>
				</div>
				<div className="flexbox--flex-wrap">
					<FormTextInput name={`${path}.swift`} label={'Swift kode'} size={'small'} />
					<FormSelect
						name={`${path}.landkode`}
						label={'Land'}
						kodeverk={GtKodeverk.LAND}
						size={'large'}
						afterChange={updateSwiftLandkode}
					/>
					<FormTextInput name={`${path}.banknavn`} label={'Banknavn'} size={'small'} />
					<FormTextInput name={`${path}.iban`} label={'Bankkode'} />
					<FormSelect
						name={`${path}.valuta`}
						label="Valuta"
						kodeverk={ArbeidKodeverk.Valutaer}
						size={'large'}
					/>
				</div>
				<div className="flexbox--flex-wrap">
					<FormTextInput name={`${path}.bankAdresse1`} label={'Adresselinje 1'} />
					<FormTextInput name={`${path}.bankAdresse2`} label={'Adresselinje 2'} />
					<FormTextInput name={`${path}.bankAdresse3`} label={'Adresselinje 3'} />
				</div>
			</div>
		</Vis>
	)
}
