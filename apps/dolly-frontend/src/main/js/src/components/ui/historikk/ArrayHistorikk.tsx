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
	identtype,
	header,
}) => {
	const Main = component
	const MainRedigerbar = componentRedigerbar
	const historikkHeader = header !== '' ? header + ' historikk' : 'Historikk'

	const getComponent = (element, idx) => {
		const pdlfElement = pdlfData?.find((item) => item.hendelseId === element.metadata.opplysningsId)
		if (element.metadata.master === 'PDL' && pdlfElement) {
			return (
				<MainRedigerbar
					idx={idx}
					data={pdlfElement}
					alleData={pdlfData}
					tmpData={tmpData}
					tmpPersoner={tmpPersoner}
					ident={ident}
					identtype={identtype}
				/>
			)
		}
		return <Main idx={idx} data={element} />
	}

	return (
		<div className="array-historikk">
			{data?.length > 0 && (
				<ErrorBoundary>
					<DollyFieldArray data={data} header={header} nested>
						{(element, idx) => {
							return getComponent(element, idx)
						}}
					</DollyFieldArray>
				</ErrorBoundary>
			)}
			{historiskData?.length > 0 && (
				<Panel heading={historikkHeader}>
					<ErrorBoundary>
						<DollyFieldArray data={historiskData} nested>
							{(element, idx) => {
								return getComponent(element, idx)
							}}
						</DollyFieldArray>
					</ErrorBoundary>
				</Panel>
			)}
		</div>
	)
}
