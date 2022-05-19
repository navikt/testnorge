import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { FormikDatepicker } from '~/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '~/config/kodeverk'
import * as React from 'react'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'

export const PdlPersonUtenIdentifikator = ({ path }) => {
	const navnInfo = SelectOptionsOppslag.hentPersonnavn()
	//@ts-ignore
	const fornavnOptions = SelectOptionsOppslag.formatOptions('fornavn', navnInfo)
	//@ts-ignore
	const mellomnavnOptions = SelectOptionsOppslag.formatOptions('mellomnavn', navnInfo)
	//@ts-ignore
	const etternavnOptions = SelectOptionsOppslag.formatOptions('etternavn', navnInfo)

	return (
		<>
			<FormikSelect name={`${path}.kjoenn`} label="Kjønn" options={Options('kjoenn')} />
			<FormikDatepicker name={`${path}.foedselsdato`} label="Fødselsdato" maxDate={new Date()} />
			<FormikSelect
				name={`${path}.statsborgerskap`}
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				size="large"
			/>
			<FormikSelect name={`${path}.navn.fornavn`} label="Fornavn" options={fornavnOptions} />
			<FormikSelect
				name={`${path}.navn.mellomnavn`}
				label="Mellomnavn"
				options={mellomnavnOptions}
			/>
			<FormikSelect name={`${path}.navn.etternavn`} label="Etternavn" options={etternavnOptions} />
		</>
	)
}
