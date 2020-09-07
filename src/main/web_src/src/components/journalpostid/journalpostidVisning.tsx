import * as React from 'react'
import _get from 'lodash/get'
import _has from 'lodash/has'
import { DollyApi } from '~/service/Api'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

interface JournalpostId {
	system: string
	ident: string
}

type Response = {
	miljoe: string
	transaksjonId: string
}

export default ({ system, ident }: JournalpostId) => (
	<LoadableComponent
		onFetch={() => {
			try {
				return DollyApi.getTransaksjonid(system, ident).then(
					({ data }): Array<Response> =>
						data.map((id: Response) => ({
							transaksjonId: id.transaksjonId,
							miljoe: id.miljoe.toUpperCase()
						}))
				)
			} catch (error) {}
		}}
		render={(data: Array<Response>, feilmelding) => {
			if (feilmelding != null) {
				return (
					<DollyFieldArray data={feilmelding} header="Feil" nested>
						<div>
							<TitleValue title="Feilmelding" value={feilmelding} />
						</div>
					</DollyFieldArray>
				)
			}
			return (
				data &&
				data.length > 0 && (
					<DollyFieldArray data={data} header="Journalpost-Id" nested>
						{(id: Response, idx: number) => {
							const transaksjonId = JSON.parse(id.transaksjonId)
							return (
								<div key={idx} className="person-visning_content">
									<TitleValue title="Miljø" value={id.miljoe} />
									<TitleValue title="Journalpost-id" value={transaksjonId.journalpostId} />
									<TitleValue title="Dokumentinfo-id" value={transaksjonId.dokumentInfoId} />
								</div>
							)
						}}
					</DollyFieldArray>
				)
			)
		}}
	/>
)
