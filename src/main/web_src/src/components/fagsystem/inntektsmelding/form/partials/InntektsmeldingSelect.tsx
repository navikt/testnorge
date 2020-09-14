import React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import LoadableComponent, { Feilmelding } from '~/components/ui/loading/LoadableComponent'
import { FormikSelect } from '~/components/ui/form/inputs/select/Select'
import { SelectOptionsOppslag } from '~/service/SelectOptionsOppslag'
import Formatters from '~/utils/DataFormatter'
import { DollyErrorAlert } from '~/components/ui/loading/DollyErrorAlert'

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
			render={(data: Array<Option>) => (
				<FormikSelect name={path} label={label} options={data} type="text" size={size} />
			)}
		/>
	)
}
