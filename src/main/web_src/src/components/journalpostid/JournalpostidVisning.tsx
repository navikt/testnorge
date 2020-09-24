import * as React from 'react'
import { DollyApi } from '~/service/Api'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import LoadableComponent from '~/components/ui/loading/LoadableComponent'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'

interface JournalpostId {
	system: string
	ident: string
}

type Response = {
	miljoe: string
	transaksjonId: string
}

export default ({ system, ident }: JournalpostId) => (
	<ErrorBoundary>
		<LoadableComponent
			onFetch={() => {
				return DollyApi.getTransaksjonid(system, ident).then(
					({ data }): Array<Response> => {
						return data.map((id: Response) => ({
							transaksjonId: id.transaksjonId,
							miljoe: id.miljoe
						}))
					}
				)
			}}
			render={(data: Array<Response>) => {
				return (
					<ErrorBoundary>
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
					</ErrorBoundary>
				)
			}}
		/>
	</ErrorBoundary>
)
