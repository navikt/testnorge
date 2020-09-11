import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Formatters from '~/utils/DataFormatter'
import { ErrorComponent } from '~/components/ui/loading/ErrorComponent'

interface InntektsmeldingSelect {
	path: string
	label: string
	kodeverk: string
	size?: string
}

type Option = {
	label: string
	value: string
	tema?: string
}

export default ({ path, label, kodeverk, size = 'medium' }: InntektsmeldingSelect) => {
	return (
		<LoadableComponent
			onFetch={() =>
				SelectOptionsOppslag.hentInntektsmeldingOptions(kodeverk).then(response =>
					response.map((value: string) => ({ value, label: Formatters.codeToNorskLabel(value) }))
				)
			}
			renderOnError={error => {
				return <ErrorComponent errorMessage={error} feilKomponent={'InntektsmeldingSelect'} />
			}}
			render={(data: Array<Option>) => {
				return <FormikSelect name={path} label={label} options={data} type="text" size={size} />
			}}
		/>
	)
}
