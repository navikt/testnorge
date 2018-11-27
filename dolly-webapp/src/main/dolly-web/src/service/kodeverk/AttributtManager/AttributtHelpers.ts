import { AttributtType, Attributt } from './Types'
import AttributtListe from './Attributter'

//TODO: Move common functions in AttributtManager to this file.

//TODO: Implement resultset for attributes for selection
// export const getAttributesForSelection = (): Attributt => {}

export const getAttributterForEditing = (): Attributt[] => {
	return AttributtListe.filter(attr => isAttributtEditable(attr))
}

export const isAttributtEditable = (attr: Attributt): boolean => {
	return (
		attr.attributtType === AttributtType.SelectAndEdit ||
		attr.attributtType === AttributtType.SelectAndRead ||
		attr.attributtType === AttributtType.EditOnly
	)
}
