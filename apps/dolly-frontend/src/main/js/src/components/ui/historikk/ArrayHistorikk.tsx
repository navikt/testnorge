import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Panel from '@/components/ui/panel/Panel'
import './historikk.less'

export const ArrayHistorikk = ({
	component,
	componentRedigerbar,
	data,
	pdlfData,
	historiskData,
	tmpData,
	tmpPersoner,
	ident,
	header,
}) => {
	const Main = component
	const MainRedigerbar = componentRedigerbar
	const historikkHeader = header !== '' ? header + ' historikk' : 'Historikk'

	return (
		<div className="array-historikk">
			{data?.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={data} header={header} nested>
						{(element, idx) => {
							const pdlfElement = pdlfData?.find(
								(item) => item.hendelseId === element.metadata.opplysningsId,
							)
							if (element.metadata.master === 'PDL' && pdlfElement) {
								return (
									<MainRedigerbar
										idx={idx}
										data={pdlfElement}
										alleData={pdlfData}
										tmpData={tmpData}
										tmpPersoner={tmpPersoner}
										ident={ident}
									/>
								)
							}
							return <Main idx={idx} data={element} />
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
			{/*TODO: Hva gjør vi med historiske data?*/}
			{historiskData?.length > 0 && (
				<Panel heading={historikkHeader}>
					<ErrorBoundary>
						<DollyFieldArray data={historiskData} nested>
							{(element, idx) => <Main idx={idx} data={element} />}
						</DollyFieldArray>
					</ErrorBoundary>
				</Panel>
			)}
		</div>
	)
}
