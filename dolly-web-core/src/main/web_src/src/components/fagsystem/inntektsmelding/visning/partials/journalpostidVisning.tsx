import * as React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { DollyApi } from '~/service/Api'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

interface JournalpostId {
	ident: string
}

type Response = {
	miljoe: string
	transaksjonId: string
}

export default ({ ident }: JournalpostId) => {
	return (
		<LoadableComponent
			onFetch={() =>
				DollyApi.getTransaksjonid('INNTKMELD', ident).then(
					({ data }): Array<Response> =>
						data.map((id: Response) => ({
							transaksjonId: id.transaksjonId,
							miljoe: id.miljoe.toUpperCase()
						}))
				)
			}
			render={(data: Array<Response>) =>
				data.length > 0 && (
					<DollyFieldArray data={data} header="Journalpost-Id" nested>
						{(id: Response, idx: number) => (
							<div key={idx} className="person-visning_content">
								<TitleValue title="Miljø" value={id.miljoe} />
								<TitleValue title="Journalpost-id" value={id.transaksjonId} />
							</div>
						)}
					</DollyFieldArray>
				)
			}
		/>
	)
}
