import { cctvView } from '@/modules/cctv';
// import cctvModule from "@/modules/cctv";
import { StyleSheet, Text } from 'react-native';

export default function HomeScreen() {
  return (
    <>
      <Text style={{"color":"black"}}>hello</Text>
      {/* <Text>{cctvModule.PI}</Text> */}
      <cctvView style={[]} onImageTaken={()=>{}}/>
    </>
  );
}

const styles = StyleSheet.create({
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
  stepContainer: {
    gap: 8,
    marginBottom: 8,
  },
  reactLogo: {
    height: 178,
    width: 290,
    bottom: 0,
    left: 0,
    position: 'absolute',
  },
});
