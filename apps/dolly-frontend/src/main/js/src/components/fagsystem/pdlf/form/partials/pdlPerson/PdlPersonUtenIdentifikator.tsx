import { FormSelect } from '@/components/ui/form/inputs/select/Select'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { FormDatepicker } from '@/components/ui/form/inputs/datepicker/Datepicker'
import { AdresseKodeverk } from '@/config/kodeverk'
import * as React from 'react'
import { useGenererNavn } from '@/utils/hooks/useGenererNavn'
import { SelectOptionsFormat } from '@/service/SelectOptionsFormat'
import { UseFormReturn } from 'react-hook-form/dist/types'

interface PdlPersonUtenIdentifikatorValues {
	formMethods: UseFormReturn
	path: string
}

export const PdlPersonUtenIdentifikator = ({
	formMethods,
	path,
}: PdlPersonUtenIdentifikatorValues) => {
	const { navnInfo, loading } = useGenererNavn()
	//@ts-ignore
	const fornavnOptions = SelectOptionsFormat.formatOptions('fornavn', navnInfo)
	//@ts-ignore
	const mellomnavnOptions = SelectOptionsFormat.formatOptions('mellomnavn', navnInfo)
	//@ts-ignore
	const etternavnOptions = SelectOptionsFormat.formatOptions('etternavn', navnInfo)

	const { fornavn, mellomnavn, etternavn } = formMethods.watch(`${path}.navn`)

	return (
		<div className={'flexbox--flex-wrap'} style={{ marginTop: '10px' }}>
			<FormSelect name={`${path}.kjoenn`} label="Kjønn" options={Options('kjoenn')} />
			<FormDatepicker name={`${path}.foedselsdato`} label="Fødselsdato" maxDate={new Date()} />
			<FormSelect
				name={`${path}.statsborgerskap`}
				label="Statsborgerskap"
				kodeverk={AdresseKodeverk.StatsborgerskapLand}
				size="large"
			/>
			<FormSelect
				name={`${path}.navn.fornavn`}
				label="Fornavn"
				placeholder={fornavn ? fornavn : 'Velg ...'}
				options={fornavnOptions}
				isLoading={loading}
			/>
			<FormSelect
				name={`${path}.navn.mellomnavn`}
				label="Mellomnavn"
				placeholder={mellomnavn ? mellomnavn : 'Velg ...'}
				options={mellomnavnOptions}
				isLoading={loading}
			/>
			<FormSelect
				name={`${path}.navn.etternavn`}
				label="Etternavn"
				placeholder={etternavn ? etternavn : 'Velg ...'}
				options={etternavnOptions}
				isLoading={loading}
			/>
		</div>
	)
}
